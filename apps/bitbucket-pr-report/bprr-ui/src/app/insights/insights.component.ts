import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { Chart } from 'angular-highcharts';

@Component({
  selector: 'app-insights',
  templateUrl: './insights.component.html',
  styleUrls: ['./insights.component.scss']
})
export class InsightsComponent implements OnInit {
  chart: Chart = null;

  constructor(private httpClient: HttpClient) { }

  ngOnInit() {
  }

  private plotChart(options) {
    this.chart = new Chart({
      chart: {
        plotBackgroundColor: null,
        plotBorderWidth: null,
        plotShadow: false,
        type: 'pie'
      },
      title: {
        text: options.title
      },
      tooltip: {
        pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
      },
      plotOptions: {
        pie: {
          allowPointSelect: true,
          cursor: 'pointer',
          dataLabels: {
            enabled: true,
            format: '<b>{point.name}</b>: {point.percentage:.1f} %',
            style: {
              color: 'black'
            }
          }
        }
      },
      series: [{
        name: options.seriesName,
        colorByPoint: true,
        data: options.data,
        type: 'pie',
      }]
    });
  }

  deploymentsPerRepository() {
    this.httpClient.get(environment.urls.stats + '/deployments-per-repository').subscribe((data: any[]) => {
      if (data) {
        this.plotChart({
          title: 'Deployments per repository',
          seriesName: 'Deployments',
          data: data.map(x => ({
            name: x.slug,
            y: x.count
          }))
        });
      }
    });
  }

  buildsPerUser() {
    this.httpClient.get(environment.urls.stats + '/builds-per-user').subscribe((data: any[]) => {
      if (data) {
        this.plotChart({
          title: 'Builds per user',
          seriesName: 'Builds',
          data: data.map(x => ({
            name: x.username,
            y: x.count
          }))
        });
      }
    });
  }

  prsPerUser() {
    this.httpClient.get(environment.urls.stats + '/prs-per-user').subscribe((data: any[]) => {
      if (data) {
        this.plotChart({
          title: 'PRs per user',
          seriesName: 'PRs',
          data: data.map(x => ({
            name: x.username,
            y: x.count
          }))
        });
      }
    });
  }
}
