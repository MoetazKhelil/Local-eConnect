import { PostType } from './post-type';
import { UserProfile } from './user';

export interface Post {
  id?: number;
  author?: UserProfile;
  date?: string;
  authorID: number | undefined;
  content: string;
  images: string[];
  likedByUser: boolean;
  comments?: Comment[];
  likes?: number;
  postType: PostType;
  isLikingInProgress?: boolean;
}
export interface Like {
  id?: number;
  likerId: number;
}
export interface Comment {
  id?: number;
  authorID: number;
  date: string;
  text: string;
  authorName?: string;
}
export interface FollowerProfile {
  userId?: number;
  name: string;
  userName: string;
  isFollowing: boolean;
  profilePicture: string;
}
