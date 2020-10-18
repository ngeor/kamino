import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'ago'
})
export class AgoPipe implements PipeTransform {

  transform(value: any, ...args: any[]): any {
    if (!value) {
      return 'N/A';
    }

    // value is in UTC, but it is considered to be local when parsing, which causes an issue
    const dt = new Date(value);
    const offset = dt.getTimezoneOffset() * 60 * 1000;
    const now = new Date();
    const diff = now.getTime() - dt.getTime() + offset;
    return this.millisecondsOrLarger(diff);
  }

  private millisecondsOrLarger(msec) {
    if (msec < 1000) {
      return `${msec} msec ago`;
    }

    return this.secondsOrLarger(msec / 1000);
  }

  private secondsOrLarger(sec) {
    if (sec < 60) {
      return `${Math.round(sec)}'' ago`;
    }

    return this.minutesOrLarger(sec / 60);
  }

  private minutesOrLarger(minutes) {
    return `${Math.round(minutes)}' ago`;
  }
}
