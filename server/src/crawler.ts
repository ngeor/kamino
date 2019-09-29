import axios from "axios";
import cheerio from "cheerio";
import { MongoClient, Collection } from "mongodb";

interface SavedUrl {
  url: string;
  responseStatus: number;
  visitStatus: "never" | "visiting" | "visited";
  visitedAt?: Date;
}

async function mongoUrls<T>(
  fn: (collection: Collection<SavedUrl>) => Promise<T>
): Promise<T> {
  const client = await MongoClient.connect("mongodb://localhost:27017");
  const db = client.db("buzz-stats");
  const collection = db.collection("urls");
  await collection.createIndex({ url: 1 }, { unique: true });
  const result = await fn(collection);
  await client.close();
  return result;
}

async function enqueueForce(collection: Collection<SavedUrl>, url: string) {
  const savedUrl: SavedUrl = {
    url,
    responseStatus: 0,
    visitStatus: "never"
  };

  await collection.replaceOne({ url }, savedUrl, { upsert: true });
}

async function enqueueNew(collection: Collection<SavedUrl>, url: string) {
  const savedUrl: SavedUrl = {
    url,
    responseStatus: 0,
    visitStatus: "never"
  };

  await collection.updateOne(
    { url },
    { $setOnInsert: savedUrl },
    { upsert: true }
  );
}

async function processNext(collection: Collection<SavedUrl>) {
  const result = await collection.findOneAndUpdate(
    { visitStatus: "never" },
    { $set: { visitStatus: "visiting" } }
  );

  if (!result.value) {
    return;
  }

  const url = result.value.url;
  console.log(`Fetching ${url}`);
  const response = await axios.get(url);
  await collection.findOneAndUpdate(
    { url },
    { $set: { visitStatus: "visited", responseStatus: response.status, visitedAt: new Date() } }
  );

  if (response.status !== 200) {
    console.log(`Got unexpected response ${response.status}`);
    return;
  }

  const $ = cheerio.load(response.data);
  const links: string[] = $("a")
    .map((idx, elm) => $(elm).attr("href"))
    .get()
    .filter(
      href =>
        href &&
        typeof href === "string" &&
        href.startsWith("https://shop.bestseller.com/")
    );
  return Promise.all(links.map(link => enqueueNew(collection, link)));
}

mongoUrls(collection => enqueueForce(collection, "https://shop.bestseller.com/"));
setInterval(() => mongoUrls(processNext), 1000);
