import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, FontAwesomeModule],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
/**
 * This class contains runnable methods ued in HomeComponent.
 * @author Chamodh
 */
export class HomeComponent implements OnInit, OnDestroy {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/config';
  private stopUrl = 'http://localhost:8080/api/stop';
  private startUrl = 'http://localhost:8080/api/start';
  private webSocket: WebSocket | null = null;
  isConnected: boolean = false;
  isStopping: boolean = false;
  poolSize: number = 0;
  fetchedConfiguration: any = {
    totalTickets: 0,
    releaseRate: 0,
    buyingRate: 0,
    maxCapacity: 0,
  };

  /**
   * This method intiates on the run contains which the loading from the previous configuration.
   */
  ngOnInit(): void {
    this.loadFromFile();
  }

  /**
   * This method loads the previous configuration from the api URL. (Backend URL)
   */
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

  /**
   * This method establishes the connection of frontend and the Web soket used in backend.
   * If the connection already established or stopping, it prevents starting a new connection.
   * The method initailizes the websocket, sends a POST request to start the application,
   * and sets up websockets for open, message, error and close events.
   * @returns void
   */
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

  /**
   * The method disconnects frontend and backend with the websocke.
   * If the websocket connection is already disconnceted it console logs a message
   * and returns.
   * @returns void
   */
  stopConnection() {
    if (!this.webSocket || !this.isConnected) {
      console.log('Websocket already disconnected.');
      return;
    }
    this.isStopping = true;
    this.http.post(this.stopUrl, {}).subscribe(() => {
      console.log('Application Stopped.');
      this.isStopping = false;
      this.isConnected = false;
    });
  }

  /**
   * This ends the life cycle of the connection when the component is destroyed.
   * This closes websocket if it was still open.
   */
  ngOnDestroy(): void {
    if (this.webSocket) {
      this.webSocket.close();
    }
  }
}
