import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { Observable, Subject } from 'rxjs';
import { HorseTree } from 'src/app/dto/horse';
import { HorseService } from 'src/app/service/horse.service';

@Component({
  selector: 'app-horse-tree',
  templateUrl: './horse-tree.component.html',
  styleUrls: ['./horse-tree.component.scss']
})
export class HorseTreeComponent implements OnInit {

  public deleteSubject = new Subject<HorseTree>();
  public generations = 5;
  public tree$?: Observable<HorseTree>;
  private id?: number;

  constructor(
    private horses: HorseService,
    private route: ActivatedRoute,
    private router: Router,
    private notification: ToastrService
  ) {
    this.deleteSubject.subscribe(horse => {
      this.horses.delete(horse.id).subscribe({
        next: (data) => {
          this.notification.success(`Horse ${horse.name} successfully deleted.`);
          if (this.id === horse.id) { this.router.navigate(['/']); }
          else { this.getAncestors(); }
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
    });
  }

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (Number.isNaN(id)) {
      this.notification.error('No horse selected');
      this.router.navigate(['/']);
    }
    this.id = id;
    this.getAncestors();
  }

  getAncestors() {
    if (Number.isNaN(this.id) || this.id === undefined) {
      throw new Error('No horse selected');
    }
    this.tree$ = this.horses.getAncestors(this.id, this.generations);
  }
}
