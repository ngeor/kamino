import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Component({
  selector: 'app-users',
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.scss']
})
export class UsersComponent implements OnInit {
  users$: Observable<any>;

  constructor(private httpClient: HttpClient) {
    this.users$ = httpClient.get(environment.urls.users + '?sort=displayName')
      .pipe(
        map((x: any) => x._embedded.users)
      );
  }

  ngOnInit() {
  }

}
