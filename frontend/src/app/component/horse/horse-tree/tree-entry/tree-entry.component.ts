import { animate, state, style, transition, trigger } from '@angular/animations';
import { Component, Input, OnInit } from '@angular/core';
import { Subject } from 'rxjs';
import { HorseTree } from 'src/app/dto/horse';
import { Sex } from 'src/app/dto/sex';
import { FamilytreeService } from 'src/app/service/familytree.service';

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

  private _horse: HorseTree | undefined;
  private expanded = true;

  constructor(private familyTree: FamilytreeService) { }


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

  // eslint-disable-next-line no-underscore-dangle
  get horse(): HorseTree | undefined { return this._horse; }

  @Input()
  set horse(horse: HorseTree | undefined) {
    // eslint-disable-next-line no-underscore-dangle
    this._horse = horse;
    if (horse) { this.expanded = this.familyTree.isExpanded(horse.id); }
  };

  toggle() {
    this.expanded = !this.expanded;
    if (this.horse) { this.familyTree.setExpanded(this.horse.id, this.expanded); }
  }

  delete(horse: HorseTree) {
    this.familyTree.delete(horse);
  }
}
