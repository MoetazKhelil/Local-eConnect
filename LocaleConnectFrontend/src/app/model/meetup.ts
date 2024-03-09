export interface Meetup {
  id: number;
  creatorId: number;
  creatorName?: string;
  name: string;
  description: string;
  date: Date;
  startTime: string;
  endTime: string;
  cost: number;
  location: string;
  spokenLanguages: string[];
  meetupAttendees: number[];
  rating: number;
  ratingSubmitted?: boolean;
  ratingsCount?: number;
  averageRating?: number;
  expand?: boolean;
  isAttending?: boolean;
}
