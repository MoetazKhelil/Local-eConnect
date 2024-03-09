import {formatDate} from "@angular/common";

export function formatToDateString(date: Date, locale: string = 'de-DE'): string {
  return formatDate(date, 'yyyy-MM-dd', locale);
}

export function getFormattedDateAndTime(date: Date): string {
  const pad = (num: number) => (num < 10 ? '0' : '') + num;
  const day = pad(date.getDate());
  const month = pad(date.getMonth() + 1);
  const year = date.getFullYear();
  const hours = pad(date.getHours());
  const minutes = pad(date.getMinutes());
  const seconds = pad(date.getSeconds());

  return `${day}-${month}-${year} ${hours}:${minutes}:${seconds}`;
}
