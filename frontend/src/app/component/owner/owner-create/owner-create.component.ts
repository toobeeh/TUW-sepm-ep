import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { NgForm, NgModel } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { Observable } from 'rxjs';
import { Owner } from 'src/app/dto/owner';
import { OwnerService } from 'src/app/service/owner.service';

@Component({
  selector: 'app-owner-create',
  templateUrl: './owner-create.component.html',
  styleUrls: ['./owner-create.component.scss'],
})
export class OwnerCreateComponent {
  owner: Owner = {
    firstName: '',
    lastName: '',
    email: '',
  };

  constructor(
    private ownerService: OwnerService,
    private router: Router,
    private route: ActivatedRoute,
    private notification: ToastrService
  ) {}

  public dynamicCssClassesForInput(input: NgModel): any {
    return {
      // This names in this object are determined by the style library,
      // requiring it to follow TypeScript naming conventions does not make sense.
      // eslint-disable-next-line @typescript-eslint/naming-convention
      'is-invalid': !input.valid && !input.pristine,
    };
  }

  public onSubmit(form: NgForm): void {
    console.log('is form valid?', form.valid, this.owner);
    if (form.valid) {
      const observable = this.ownerService.create(this.owner);
      observable.subscribe({
        next: (data) => {
          this.notification.success(
            `Owner ${this.owner.firstName} ${this.owner.lastName} successfully created.`
          );
          this.router.navigate(['/owners']);
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
          this.notification.error(
            `Owner ${this.owner.firstName} ${this.owner.lastName} could not be created: \n${message}`
          );
          console.error('Error creating owner', response);
        },
      });
    }
  }
}
