import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';

@Component({
  selector: 'app-create-config',
  standalone: true,
  imports: [CommonModule, FontAwesomeModule, FormsModule],
  templateUrl: './create-config.component.html',
  styleUrl: './create-config.component.scss',
})

/**
 * The cerate configure component class is responsible for handling the creation of the configuration settings.
 * This components allows users to input configuration details, which are then sent to the backend API to be saved.
 */
export class CreateConfigComponent {
  config: any = {
    totalTickets: 0,
    releaseRate: 0,
    buyingRate: 0,
    maxCapacity: 0,
  };
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/config/create';
  private router = inject(Router);

  /**
   * Submits the current configuration data to the backend API.
   * Sends a POST request with the configuration data in JSON format.
   * Handles the responses by logging success or error messages.
   */
  submitConfig() {
    this.http
      .post(this.apiUrl, this.config, {
        headers: { 'Content-Type': 'application/json' },
      })
      .subscribe({
        next: (data) => {
          console.log('Configuration added successfully.', data);
          this.router.navigate(['/']);
        },
        error: (error) => {
          console.log('Error adding configuration.', error);
        },
      });
  }
}
