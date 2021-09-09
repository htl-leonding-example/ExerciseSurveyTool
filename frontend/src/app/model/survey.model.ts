export class Survey {
  text: string = "";
  result: Map<string, number>;

  constructor() {
    this.result = new Map();
  }
}
