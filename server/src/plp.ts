import axios from "axios";
import cheerio from "cheerio";
import { EventEmitter } from "events";

export const productFoundEventEmitter = new EventEmitter();

export async function getPlpPage() {
  console.log("calling");
  const url = "https://shop.bestseller.com/nl/nl/bc/heren/nieuw/jeans/";
  const response = await axios.get(url);
  console.log("called");
  console.log(response.status);

  const $ = cheerio.load(response.data);
  $("article.product-tile").each((idx, elm)=> {
    const data = JSON.parse($(elm).attr("data-layer-impression"));
    productFoundEventEmitter.emit("product", data);
  });
}
