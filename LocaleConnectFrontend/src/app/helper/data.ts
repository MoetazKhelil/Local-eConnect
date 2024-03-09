// private mockFollowers: FollowerProfile[] = [
// {
// userId: 1,
// name: 'Alice Johnson',
// username: 'alicej',
// isFollowing: true,
// profileImage: 'https://www.profilebakery.com/wp-content/uploads/2023/04/AI-Profile-Picture.jpg',
// },
// {
// userId: 2,
// name: 'Bob Smith',
// username: 'bobsmith',
// isFollowing: false,
// profileImage: 'https://www.profilebakery.com/wp-content/uploads/2023/04/Profile-Image-AI.jpg',
// },
// {
// userId: 3,
// name: 'Charlie Brown',
// username: 'charlieb',
// isFollowing: true,
// profileImage: 'https://i.pinimg.com/originals/c2/6d/91/c26d912eb43a85ca51ecb81804f36dc4.jpg',
// },
// {
// userId: 4,
// name: 'Diana Prince',
// username: 'dianap',
// isFollowing: false,
// profileImage: 'assets/pictures/profil.png',
// },
// {
// userId: 5,
// name: 'Edward Norton',
// username: 'edwardn',
// isFollowing: true,
// profileImage: 'https://cdn2.unrealengine.com/egs-evil-dead-featured-carousel-desktop-image-01-1248x702-0f90c4bf044e.jpg?h=1080&resize=1&w=1920',
// },
// {
// userId: 6,
// name: 'Fiona Gallagher',
// username: 'fionag',
// isFollowing: false,
// profileImage: 'assets/pictures/profil.png',
// },
// ];
//
// private mockPosts: Post[] = [
// {
// id: 1,
// author: this.mockFollowers[0],
// date: new Date('2024-02-01T08:45:00'),
// content: 'Just had a fantastic trip to New Zealand! 🏞️ #travel #adventure',
// likes: 3,
// images: [],
// comments: [
// {
// id: 101,
// author: this.mockFollowers[3],
// date: new Date('2024-02-01T09:00:00'),
// text: 'Wow, looks amazing! Glad you had a great time.',
// },
// {
// id: 102,
// author: this.mockFollowers[1],
// date: new Date('2024-02-01T09:30:00'),
// text: 'New Zealand is on my bucket list too!',
// }
// ]
// },
// {
// id: 2,
// author: this.mockFollowers[1],
// date: new Date('2024-02-02T10:20:00'),
// content: 'Exploring the local market scenes. So much to see and taste! 🍲🥘',
// images: ['https://imgs.search.brave.com/9BBcHP36m3hbc0nG8qPoaP1TCvla5FZAUocLypeJ43Y/rs:fit:860:0:0/g:ce/aHR0cHM6Ly9idXJz/dC5zaG9waWZ5Y2Ru/LmNvbS9waG90b3Mv/bWFya2V0LXBlcHBl/cnMuanBnP3dpZHRo/PTEwMDAmZm9ybWF0/PXBqcGcmZXhpZj0w/JmlwdGM9MA',
// 'https://imgs.search.brave.com/7E-VAKUDsP304dLxX8_8ZLKR0btjWuggda5qtGZmpwU/rs:fit:860:0:0/g:ce/aHR0cHM6Ly9tZWRp/YS5pc3RvY2twaG90/by5jb20vaWQvNTEw/OTQ4OTYyL3Bob3Rv/L3dldC1tYXJrZXQt/aW4taG9uZy1rb25n/LmpwZz9zPTYxMng2/MTImdz0wJms9MjAm/Yz12SVotXzd1X1hn/VURiWGtGejZ3ZFVz/V0VLaVlBT2FyV0Fx/cTdteG1QdUMwPQ', 'https://imgs.search.brave.com/9BBcHP36m3hbc0nG8qPoaP1TCvla5FZAUocLypeJ43Y/rs:fit:860:0:0/g:ce/aHR0cHM6Ly9idXJz/dC5zaG9waWZ5Y2Ru/LmNvbS9waG90b3Mv/bWFya2V0LXBlcHBl/cnMuanBnP3dpZHRo/PTEwMDAmZm9ybWF0/PXBqcGcmZXhpZj0w/JmlwdGM9MA',
// 'https://imgs.search.brave.com/7E-VAKUDsP304dLxX8_8ZLKR0btjWuggda5qtGZmpwU/rs:fit:860:0:0/g:ce/aHR0cHM6Ly9tZWRp/YS5pc3RvY2twaG90/by5jb20vaWQvNTEw/OTQ4OTYyL3Bob3Rv/L3dldC1tYXJrZXQt/aW4taG9uZy1rb25n/LmpwZz9zPTYxMng2/MTImdz0wJms9MjAm/Yz12SVotXzd1X1hn/VURiWGtGejZ3ZFVz/V0VLaVlBT2FyV0Fx/cTdteG1QdUMwPQ'
// ],
// comments: [],
// likes: 5
// },
// {
// id: 3,
// author: this.mockFollowers[2],
// date: new Date('2024-02-03T14:35:00'),
// content: 'Nothing beats the feeling of reaching the summit! ⛰️ #hiking #mountains',
// images: [
// 'https://imgs.search.brave.com/v6Xb532u4sPjwCpMXQbi-R5hyMkxl41UP894qNDi_fA/rs:fit:860:0:0/g:ce/aHR0cHM6Ly9tZWRp/YS5nZXR0eWltYWdl/cy5jb20vaWQvMTE4/NDE5NTA1Ni9waG90/by9zb2xvLWhpa2Vy/LXdhbGtpbmctb24t/YS1oaWdoLW1vdW50/YWluLXBsYWluLmpw/Zz9zPTYxMng2MTIm/dz0wJms9MjAmYz1i/WjlseS1fVWstM1F0/SzY1MmxBa09RMjFx/aEhaTUoxRTNfZUw0/NjZoMFg0PQ'
// ],
// comments: [
// {
// id: 103,
// author: this.mockFollowers[0],
// date: new Date('2024-02-03T15:15:00'),
// text: 'Incredible view! Which trail did you take?',
// }
// ],
// }
// ];
