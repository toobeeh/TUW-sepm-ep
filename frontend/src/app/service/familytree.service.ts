import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import { Horse, HorseTree } from '../dto/horse';

/**
 * A service that handles the state of a familytree
 */
@Injectable({
  providedIn: 'root'
})
export class FamilytreeService {

  private deleteSubject = new Subject<HorseTree>();
  private expansionMap = new Map<number, boolean>();

  constructor() { }

  /**
   * emit a new delete event for the tree
   *
   * @param horse the horse to delete
   */
  delete(horse: HorseTree) {
    this.deleteSubject.next(horse);
  }

  /**
   * adds a new callback to delete events
   *
   * @param callback the callback function, has the horse to delete as parameter
   * @returns a subscription of the delete subject
   */
  onDelete(callback: (horse: HorseTree) => void) {
    return this.deleteSubject.subscribe(horse => callback(horse));
  }

  /**
   * get the expanded state for a horse node
   *
   * @param id the horse's id
   * @returns the horse's last expanded state; or true if none found
   */
  isExpanded(id: number) {
    const value = this.expansionMap.get(id);
    return value === undefined || value;
  }

  /**
   * sets a horse's last expanded state
   *
   * @param id the horse's id
   * @param expanded the horse's expanded state
   */
  setExpanded(id: number, expanded: boolean) {
    this.expansionMap.set(id, expanded);
  }
}
