import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { HttpClient } from '@angular/common/http';

export interface Credentials {
  owner: string;
  username: string;
  password: string;
}

@Injectable({
  providedIn: 'root'
})
export class LoginService {

  constructor(private httpClient: HttpClient) { }

  getCredentials(): Credentials {
    return JSON.parse(window.localStorage.getItem('credentials'));
  }

  setCredentials(credentials: Credentials) {
    const url = environment.urls.credentials;
    this.httpClient.post(url, credentials).subscribe(
      _ => {
        window.localStorage.setItem('credentials', JSON.stringify(credentials));
      }
    );
  }
}
