import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { faL } from '@fortawesome/free-solid-svg-icons';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, FontAwesomeModule],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent implements OnInit, OnDestroy {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/config/load';
  private stopUrl = 'http://localhost:8080/api/stop';
  private startUrl = 'http://localhost:8080/api/start';
  private webSocket: WebSocket | null = null;
  isConnected: boolean = false;
  isStopping: boolean = false; // Add flag to track stopping process
  poolSize: number = 0;

  fetchedConfiguration: any = {
    totalTickets: 0,
    releaseRate: 0,
    buyingRate: 0,
    maxCapacity: 0,
  };

  ngOnInit(): void {
    this.loadFromFile();
  }

  loadFromFile() {
    this.http.get(this.apiUrl).subscribe({
      next: (data) => {
        this.fetchedConfiguration = data;
        console.log(
          'Fetched configuration from file: ',
          this.fetchedConfiguration
        );
      },
      error: (error) => {
        console.log('Error fetching from file: ', error);
      },
    });
  }

  startConnection() {
    if (this.isConnected || this.isStopping) {
      console.log('Cannot start while connected or stopping.');
      return;
    }

    this.webSocket = new WebSocket('ws://localhost:8080/log');

    this.http.post(this.startUrl, {}).subscribe(() => {
      console.log('Application Started.');
    });

    this.webSocket.onopen = () => {
      console.log('Websocket connection opened.');
    };

    this.webSocket.onmessage = (event) => {
      const data = event.data;
      const poolSize = Number(data);

      if (!isNaN(poolSize)) {
        this.poolSize = poolSize;
      } else {
        console.log(data);
      }
    };

    this.webSocket.onerror = (error) => {
      console.log('An error occurred. ', error);
    };

    this.webSocket.onclose = () => {
      console.log('Websocket connection closed.');
      this.isConnected = false;
      this.webSocket = null;
    };

    this.isConnected = true;
    console.log('Websocket Connected.');
  }

  stopConnection() {
    if (!this.webSocket || !this.isConnected) {
      console.log('Websocket already disconnected.');
      return;
    }

    this.isStopping = true; // Set stopping flag to true
    this.http.post(this.stopUrl, {}).subscribe(() => {
      console.log('Application Stopped.');
      this.isStopping = false; // Reset stopping flag
      this.isConnected = false; // Ensure isConnected is reset
    });
  }

  ngOnDestroy(): void {
    if (this.webSocket) {
      this.webSocket.close();
    }
  }
}
