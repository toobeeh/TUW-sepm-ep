import { HttpErrorResponse } from '@angular/common/http';
import {Component, OnInit} from '@angular/core';
import {NgForm, NgModel} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {ToastrService} from 'ngx-toastr';
import {map, Observable, of} from 'rxjs';
import {Horse} from 'src/app/dto/horse';
import {Owner} from 'src/app/dto/owner';
import {Sex} from 'src/app/dto/sex';
import {HorseService} from 'src/app/service/horse.service';
import {OwnerService} from 'src/app/service/owner.service';


export enum HorseCreateEditMode {
  create,
  edit,
  view
};

@Component({
  selector: 'app-horse-create-edit',
  templateUrl: './horse-create-edit.component.html',
  styleUrls: ['./horse-create-edit.component.scss']
})
export class HorseCreateEditComponent implements OnInit {

  public readonly modes = HorseCreateEditMode;
  mode: HorseCreateEditMode = HorseCreateEditMode.create;
  horse: Horse = {
    name: '',
    description: '',
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // @ts-ignore to use default date as empty for invalid form
    dateOfBirth: null,
    sex: Sex.female,
  };


  constructor(
    private service: HorseService,
    private ownerService: OwnerService,
    private router: Router,
    private route: ActivatedRoute,
    private notification: ToastrService,
  ) {
  }

  public get heading(): string {
    switch (this.mode) {
      case HorseCreateEditMode.create:
        return 'Create New Horse';
      case HorseCreateEditMode.edit:
        return 'Edit Horse ' + this.horse.name;
      case HorseCreateEditMode.view:
        return 'Details of Horse ' + this.horse.name;
      default:
        return '?';
    }
  }

  public get submitButtonText(): string {
    switch (this.mode) {
      case HorseCreateEditMode.create:
        return 'Create';
      case HorseCreateEditMode.edit:
        return 'Edit';
      default:
        return '?';
    }
  }

  get modeIsCreate(): boolean {
    return this.mode === HorseCreateEditMode.create;
  }

  get modeIsView(): boolean {
    return this.mode === HorseCreateEditMode.view;
  }


  private get modeActionFinished(): string {
    switch (this.mode) {
      case HorseCreateEditMode.create:
        return 'created';
      case HorseCreateEditMode.edit:
        return 'updated';
      default:
        return '?';
    }
  }

  ownerSuggestions = (input: string) => (input === '')
    ? of([])
    : this.ownerService.searchByName(input, 5);

  parentSuggestions = (input: string) => (input === '')
    ? of([])
    : this.service.getAll().pipe(map(horses => horses.filter(horse => horse.name.indexOf(input) === 0).slice(0,5)));

  ngOnInit(): void {
    this.route.data.subscribe(data => {
      this.mode = data.mode;
      if(data.mode !== HorseCreateEditMode.create){
        const id = Number(this.route.snapshot.paramMap.get('id'));
        if(Number.isNaN(id)){
          this.notification.error('Could not load horse to edit');
          this.router.navigate(['/horses']);
        }
        else {
          this.service.get(id).subscribe({
            next: horse => {
              this.horse = horse;
            },
            error: response => {
              this.notification.error('Could not load horse to edit');
              this.router.navigate(['/horses']);
            }
          });
        }
      }
    });
  }

  public dynamicCssClassesForInput(input: NgModel): any {
    return {
      // This names in this object are determined by the style library,
      // requiring it to follow TypeScript naming conventions does not make sense.
      // eslint-disable-next-line @typescript-eslint/naming-convention
      'is-invalid': !input.valid && !input.pristine,
    };
  }

  public formatOwnerName(owner: Owner | null | undefined): string {
    return (owner == null)
      ? ''
      : `${owner.firstName} ${owner.lastName}`;
  }

  public formatParentName(parent: Horse | null | undefined): string {
    return (parent == null)
      ? ''
      : parent.name;
  }


  public onSubmit(form: NgForm): void {
    console.log('is form valid?', form.valid, this.horse);
    if (form.valid) {
      if (this.horse.description === '') {
        delete this.horse.description;
      }
      let observable: Observable<Horse>;
      switch (this.mode) {
        case HorseCreateEditMode.create:
          observable = this.service.create(this.horse);
          break;
        case HorseCreateEditMode.edit:
          observable = this.service.update(this.horse);
          break;
        default:
          console.error('Unknown HorseCreateEditMode', this.mode);
          return;
      }
      observable.subscribe({
        next: data => {
          this.notification.success(`Horse ${this.horse.name} successfully ${this.modeActionFinished}.`);
          this.router.navigate(['/horses']);
        },
        error: (response: HttpErrorResponse) => {
          let message: string;
          switch (response.status) {
            case 422:
              message = response.error.errors.join(', ');
              break;
            case 409:
              message = response.error.errors.join(', ');
              break;
            default:
              message = 'Something went wrong';
              break;
          }
          this.notification.error(`Horse ${this.horse.name} could not be ${this.modeActionFinished}: \n${message}`);
          console.error('Error creating/updating horse', response, this.modeIsCreate);
        }
      });
    }
  }

  public onDelete(): void {
    let observable: Observable<Horse>;
    if(this.mode === HorseCreateEditMode.edit && this.horse.id){
      this.service.delete(this.horse.id).subscribe({
        next: data => {
          this.notification.success(`Horse ${this.horse.name} successfully deleted.`);
          this.router.navigate(['/horses']);
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
          this.notification.error(`Horse ${this.horse.name} could not be deleted: \n${message}`);
          console.error('Error deleting horse', response, this.modeIsCreate);
        }
      });
    }
    else {
      console.error('tried to remove horse in create mode or without id set');
    }
  }
}
