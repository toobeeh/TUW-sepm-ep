import { HttpErrorResponse } from '@angular/common/http';
import {Component, OnInit} from '@angular/core';
import {ToastrService} from 'ngx-toastr';
import {HorseService} from 'src/app/service/horse.service';
import {Horse} from '../../dto/horse';
import {Owner} from '../../dto/owner';

@Component({
  selector: 'app-horse',
  templateUrl: './horse.component.html',
  styleUrls: ['./horse.component.scss']
})
export class HorseComponent implements OnInit {
  search = false;
  horses: Horse[] = [];
  bannerError: string | null = null;

  constructor(
    private service: HorseService,
    private notification: ToastrService,
  ) { }

  ngOnInit(): void {
    this.reloadHorses();
  }

  reloadHorses() {
    this.service.getAll()
      .subscribe({
        next: data => {
          this.horses = data;
        },
        error: error => {
          console.error('Error fetching horses', error);
          this.bannerError = 'Could not fetch horses: ' + error.message;
          const errorMessage = error.status === 0
            ? 'Is the backend up?'
            : error.message.message;
          this.notification.error(errorMessage, 'Could Not Fetch Horses');
        }
      });
  }

  ownerName(owner: Owner | null): string {
    return owner
      ? `${owner.firstName} ${owner.lastName}`
      : '';
  }

  dateOfBirthAsLocaleDate(horse: Horse): string {
    return new Date(horse.dateOfBirth).toLocaleDateString();
  }

  public delete(id: number, horse: Horse): void {
    this.service.delete(id).subscribe({
      next: data => {
        this.notification.success(`Horse ${horse.name} successfully deleted.`);
        this.reloadHorses();
      },
      error: (response: HttpErrorResponse) => {
        let message: string;
        switch (response.status) {
          case 404:
            message = 'Horse does not exist';
            break;
          default:
            message = 'Something went wrong';
            break;
        }
        this.notification.error(`Horse ${horse.name} could not be deleted: \n${message}`);
        console.error('Error deleting horse', response);
      }
    });
  }

}
