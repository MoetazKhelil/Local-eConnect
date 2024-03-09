export interface Review {
  id?: number;
  userId: number;
  entityId: number;
  entityType: 'itinerary' | 'meetup' | 'trip';
  text?: string;
  timestamp?: string;
  rating: number;
}
