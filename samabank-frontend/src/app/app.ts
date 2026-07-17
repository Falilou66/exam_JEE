import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { ConfirmDialog } from './shared/ui/confirm-dialog';
import { RouteProgress } from './shared/ui/route-progress';
import { ToastContainer } from './shared/ui/toast-container';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, ToastContainer, RouteProgress, ConfirmDialog],
  templateUrl: './app.html',
})
export class App {}
