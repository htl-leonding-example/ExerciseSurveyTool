import {Component, ElementRef, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {webSocket, WebSocketSubject} from "rxjs/webSocket";
import {Survey} from "./model/survey.model";
import {HttpClient} from "@angular/common/http";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit, OnDestroy {
  // tag::viewchild[]
  @ViewChild('canvas', {static: false}) canvas: ElementRef<HTMLCanvasElement> | undefined;
  // end::viewchild[]

  data: Survey = new Survey();

  constructor(private http: HttpClient) {
  }

// tag::paint[]
  paint() {
    const ctx = this.canvas?.nativeElement.getContext('2d');
    if (ctx == null) return;

    const cnt = this.data.result.size;
    ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);

    ctx.font = "20px Arial";
    const colors = ["black", "red", "green", "blue"];
    const bheight = ((ctx.canvas.height - 20) / cnt) - 20;
    const bwidth = (ctx.canvas.width - 100 - 5);
    let posy = 20;
    let maxValue = Math.max(...this.data.result.values());
    let i = 0;

    this.data.result.forEach((value, key) => {
      ctx.fillStyle = colors[i % colors.length];
      let actWidth = value / maxValue * bwidth;
      ctx.fillText(key, 5, posy + (bheight / 2));
      ctx.fillRect(100, posy, actWidth, bheight);
      posy += bheight + 20;
      i++;
    });
  }

  // end::paint[]

  // tag::websockets[]
  myWebSocket: WebSocketSubject<any> | undefined;

  ngOnInit(): void {
    this.myWebSocket = webSocket({
      url: 'ws://localhost:8080/survey/1',
      deserializer: msg => msg.data
    });
    this.myWebSocket.subscribe(value => {
      //this.data = new Survey();
      let json = JSON.parse(value);
      this.data.text = json.text;
      this.data.result = new Map(Object.entries(json.result));
      this.paint();
    })
  }

  // end::websockets[]

  // tag::vote[]
  vote(key: string): void {
    this.http.post('http://localhost:8080/survey/vote/' + key, '').subscribe(value => {
      console.log('voted for ' + key);
    });
  }

  // end::vote[]

  ngOnDestroy() {
    this.myWebSocket?.unsubscribe();
  }
}
