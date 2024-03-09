import { User } from './user';
import { TripPreview } from './trip';

export interface Guide extends User {
  languages: string[];
  city: string;
  rating: number;
}

//TODO: map from guide profile to guide and vices versa
export interface GuideProfile {
  id?: number;
  firstName: string;
  lastName: string;
  name: string;
  userName: string;
  bio?: string;
  visitedCountries?: string[];
  languages: string[];
  city: string;
  rating: number;
  ratingSubmitted?: boolean;
  ratingsCount?: number;
  averageRating?: number;
  expand?: boolean;
  trips?: TripPreview[];
  isFollowing?: boolean;
  profilePicture?: string;
  followers?: User[];
}
