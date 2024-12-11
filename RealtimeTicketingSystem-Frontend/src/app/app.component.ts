import { Component, inject, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { FaConfig, FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { fontAwesomeIcons } from './shared/FontAwesomeIcons';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})

/**
 * The AppComponent serves as the root component of the application.
 * It initalizes FontAwesome icon pack and sets up default configuration for FontAwesome.
 */
export class AppComponent implements OnInit {
  private faIconLibrary = inject(FaIconLibrary);
  private faConfig = inject(FaConfig);
  title = 'RealtimeTicketingSystem-Frontend';

  /**
   * This is the life cycle hook that is called after the component is initalized.
   * This calls the method to initialize font awesome icons.
   */
  ngOnInit(): void {
    this.intFontAwesome();
  }

  /**
   * Initializes font awesome icons by the default prefix and adding icons to the
   * specified prefixes in the fontAwesomeIcons array.
   */
  intFontAwesome() {
    this.faConfig.defaultPrefix = 'far';
    this.faIconLibrary.addIcons(...fontAwesomeIcons);
  }
}
