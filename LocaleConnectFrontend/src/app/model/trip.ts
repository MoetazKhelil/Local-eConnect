export interface TripPreview {
  id: number;
  name: string;
  description: string;
  link: string;
}
export interface Trip {
  id: number;
  localguideId: number;
  localguideuserName: string;
  name: string;
  description?: string;
  departureTime: string;
  destination: string;
  durationInHours: number;
  durationInDays?: number;
  capacity: number;
  travelers: number[];
  tripAttendees: number[];
  languages: string[];
  placesToVisit: string[];
  dailyActivities?: string[];
  imageUrls: string[];
  isAttending?: boolean;
  rating: number;
  ratingSubmitted?: boolean;
  ratingsTotal?: number;
  ratingsCount?: number;
  averageRating?: number;
  expand?: boolean;
}

export interface TripReview {
  tripReviewId?: number;
  tripId: number;
  userId: number;
  text?: string;
  timestamp?: string;
  rating?: number;
}

export interface TripShare {
  id?: number;
  name: string;
  description?: string;
}
