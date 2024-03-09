export interface Notification {
  id: number;
  senderID: number;
  receiverID: number;
  sentAt: Date;
  message: string;
  title?: string;
}
