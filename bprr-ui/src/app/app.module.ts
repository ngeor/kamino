import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';

import { ChartModule } from 'angular-highcharts';

import { AppComponent } from './app.component';
import { PageNotFoundComponent } from './page-not-found/page-not-found.component';
import { LoginComponent } from './login/login.component';
import { InsightsComponent } from './insights/insights.component';
import { UsersComponent } from './users/users.component';
import { RepositoriesComponent } from './repositories/repositories.component';
import { AgoPipe } from './ago.pipe';

const appRoutes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'insights', component: InsightsComponent },
  { path: 'users', component: UsersComponent },
  { path: 'repositories', component: RepositoriesComponent },
  { path: '', pathMatch: 'full', redirectTo: '/insights' },
  { path: '**', component: PageNotFoundComponent }
];

@NgModule({
  declarations: [
    AppComponent,
    PageNotFoundComponent,
    LoginComponent,
    InsightsComponent,
    UsersComponent,
    RepositoriesComponent,
    AgoPipe
  ],
  imports: [
    BrowserModule,
    ChartModule,
    HttpClientModule,
    ReactiveFormsModule,
    RouterModule.forRoot(appRoutes, { enableTracing: false })
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
