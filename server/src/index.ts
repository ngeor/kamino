import { getPlpPage, productFoundEventEmitter } from "./plp";
import { MongoClient } from "mongodb";

async function saveProduct(p: { id: string }) {
  const client = await MongoClient.connect("mongodb://localhost:27017");
  const db = client.db("buzz-stats");
  const collection = db.collection("products");
  await collection.createIndex({ id: 1 }, { unique: true });
  await collection.replaceOne({ id: p.id }, p, { upsert: true });
  await client.close();
}

productFoundEventEmitter.on("product", (p) => {
  saveProduct(p);
});

console.log("hello, world!");
setInterval(getPlpPage, 5000);
