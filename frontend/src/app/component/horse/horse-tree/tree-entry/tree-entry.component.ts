import { animate, state, style, transition, trigger } from '@angular/animations';
import { Component, Input } from '@angular/core';
import { Subject } from 'rxjs';
import { HorseTree } from 'src/app/dto/horse';
import { Sex } from 'src/app/dto/sex';

@Component({
  selector: 'app-tree-entry',
  templateUrl: './tree-entry.component.html',
  styleUrls: ['./tree-entry.component.scss'],
  animations: [
    trigger('slide', [
      state('void', style({
        height: '0',
        opacity: '0'
      })),
      state('*', style({
        height: '*',
        opacity: '1'
      })),
      transition(':enter', [
        style({
          height: '0',
          opacity: '0'
        }),
        animate('250ms ease-in-out', style({
          height: '*',
          opacity: '1'
        }))
      ]),
      transition(':leave', [
        style({
          height: '*',
          opacity: '1'
        }),
        animate('250ms ease-in-out', style({
          height: '0',
          opacity: '0'
        }))
      ])
    ])
  ]
})
export class TreeEntryComponent {

  @Input()
  horse?: HorseTree;

  @Input()
  deleteSubject?: Subject<HorseTree>;

  private expanded = true;

  get ngClass() {
    return {
      // eslint-disable-next-line quote-props
      'expanded': this.expanded
    };
  }

  get genderClass() {
    return {
      // eslint-disable-next-line quote-props, @typescript-eslint/naming-convention
      'bi-gender-male': this.horse?.sex === Sex.male,
      // eslint-disable-next-line quote-props, @typescript-eslint/naming-convention
      'bi-gender-female': this.horse?.sex === Sex.female
    };
  }

  get showParents() {
    return this.expanded;
  }

  get noParentsAvailable() {
    return this.horse === undefined || (this.horse.father == null && this.horse.mother == null);
  }

  toggle() {
    this.expanded = !this.expanded;
  }

  delete(horse: HorseTree) {
    // should never occur
    if (!this.deleteSubject) {
      throw new Error('no delete subject passed');
    }
    this.deleteSubject.next(horse);
  }

}
