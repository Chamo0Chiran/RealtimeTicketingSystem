import { Routes } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { CreateConfigComponent } from './pages/create-config/create-config.component';

export const routes: Routes = [
  {
    path: '',
    component: HomeComponent,
  },
  {
    path: 'newconfig',
    component: CreateConfigComponent,
  },
];
