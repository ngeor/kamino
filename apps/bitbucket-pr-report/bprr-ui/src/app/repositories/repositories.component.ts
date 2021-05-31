import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';

@Component({
  selector: 'app-repositories',
  templateUrl: './repositories.component.html',
  styleUrls: ['./repositories.component.scss']
})
export class RepositoriesComponent implements OnInit {
  repositories: any[];

  constructor(private httpClient: HttpClient) {
  }

  ngOnInit() {
    this.httpClient.get(environment.urls.repositories + '?sort=slug&size=200').subscribe((x: any) => {
      this.repositories = x._embedded.repositories;
    });
  }

}
