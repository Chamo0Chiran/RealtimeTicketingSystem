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
export class AppComponent implements OnInit {
  private faIconLibrary = inject(FaIconLibrary);
  private faConfig = inject(FaConfig);
  title = 'RealtimeTicketingSystem-Frontend';

  ngOnInit(): void {
    this.intFontAwesome();
  }
  intFontAwesome() {
    this.faConfig.defaultPrefix = 'far';
    this.faIconLibrary.addIcons(...fontAwesomeIcons);
  }
}
