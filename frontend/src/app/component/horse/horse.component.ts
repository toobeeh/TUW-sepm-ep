import { HttpErrorResponse } from '@angular/common/http';
import {
  AfterViewInit,
  Component,
  OnDestroy,
  OnInit,
  ViewChild,
} from '@angular/core';
import { NgForm } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';
import { debounce, debounceTime, map, of, Subject, Subscription } from 'rxjs';
import { Sex } from 'src/app/dto/sex';
import { HorseService } from 'src/app/service/horse.service';
import { OwnerService } from 'src/app/service/owner.service';
import { Horse, HorseSearch } from '../../dto/horse';
import { Owner } from '../../dto/owner';

@Component({
  selector: 'app-horse',
  templateUrl: './horse.component.html',
  styleUrls: ['./horse.component.scss'],
})
export class HorseComponent implements OnInit, AfterViewInit, OnDestroy {
  @ViewChild('form', { static: true }) ngForm?: NgForm;

  horses: Horse[] = [];
  bannerError: string | null = null;
  formChanged?: Subscription;
  searchData: HorseSearch = {};

  constructor(
    private service: HorseService,
    private notification: ToastrService,
    private ownerService: OwnerService
  ) { }

  private get cleanSearchData() {
    const data = { ...this.searchData };

    // remove empty props
    if (data.description?.length === 0) {
      delete data.description;
    }
    if (data.name?.length === 0) {
      delete data.name;
    }
    if (data.owner?.length === 0) {
      delete data.owner;
    }
    if (data.sex?.length === 0) {
      delete data.sex;
    }
    return data;
  }

  ngOnInit(): void {
    this.reloadHorses();
  }

  ngAfterViewInit(): void {
    this.formChanged = this.ngForm?.valueChanges
      ?.pipe(debounceTime(250))
      .subscribe(this.reloadHorses.bind(this));
  }

  ngOnDestroy(): void {
    this.formChanged?.unsubscribe();
  }

  reloadHorses() {
    this.service.searchAll(this.cleanSearchData).subscribe({
      next: (data) => {
        this.horses = data;
      },
      error: (error) => {
        console.error('Error fetching horses', error);
        this.bannerError = 'Could not fetch horses: ' + error.message;
        const errorMessage =
          error.status === 0 ? 'Is the backend up?' : error.message.message;
        this.notification.error(errorMessage, 'Could Not Fetch Horses');
      },
    });
  }

  ownerSuggestions = (input: string) =>
    input === ''
      ? of([])
      : this.ownerService.searchByName(input, 5)
        .pipe(
          map(owners =>
            owners.map(o => [o.firstName, o.lastName].filter(s => s.length > 0).join(' '))
          ));

  public formatOwnerName(owner: Owner | null | undefined): string {
    return owner == null ? '' : `${owner.firstName} ${owner.lastName}`;
  }

  public formatAutocompleteInput(owner: string | null | undefined): string {
    return owner == null ? '' : owner;
  }

  dateOfBirthAsLocaleDate(horse: Horse): string {
    return new Date(horse.dateOfBirth).toLocaleDateString();
  }

  public delete(id: number, horse: Horse): void {
    this.service.delete(id).subscribe({
      next: (data) => {
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
        this.notification.error(
          `Horse ${horse.name} could not be deleted: \n${message}`
        );
        console.error('Error deleting horse', response);
      },
    });
  }
}
