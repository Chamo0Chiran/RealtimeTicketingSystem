import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, inject, OnInit } from '@angular/core';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, FontAwesomeModule],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss',
})
export class HomeComponent implements OnInit {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/config/load';

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
}
