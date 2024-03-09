import { TestBed } from '@angular/core/testing';

import { ImagesService } from './image.service';

describe('ImageService', () => {
  let service: ImagesService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ImagesService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
