import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { Horse, HorseSearch, HorseTree } from '../dto/horse';

const baseUri = environment.backendUrl + '/horses';

@Injectable({
  providedIn: 'root'
})
export class HorseService {

  constructor(
    private http: HttpClient,
  ) { }

  /**
   * Get all horses stored in the system
   *
   * @return observable list of found horses.
   */
  searchAll(searchData: HorseSearch = {}): Observable<Horse[]> {

    /* iterate through object and add params typesafe */
    let params = new HttpParams();
    Object.keys(searchData).forEach(key => {
      const objKey = key as keyof HorseSearch;
      const objVal = searchData[objKey];
      if (objVal !== undefined) {
        params = params.append(objKey, objVal);
      }
    });

    return this.http.get<Horse[]>(baseUri, { params });
  }

  /**
   * Get all horses stored in the system
   *
   * @return observable list of found horses.
   */
  get(id: number): Observable<Horse> {
    return this.http.get<Horse>(`${baseUri}/${id}`);
  }

  /**
   * Get all horses stored in the system
   *
   * @return observable list of found horses.
   */
  getAncestors(id: number, generations: number): Observable<HorseTree> {
    return this.http.get<HorseTree>(`${baseUri}/ancestors/${id}?generations=${generations}`);
  }

  /**
   * Create a new horse in the system.
   *
   * @param horse the data for the horse that should be created
   * @return an Observable for the created horse
   */
  create(horse: Horse): Observable<Horse> {
    return this.http.post<Horse>(
      baseUri,
      horse
    );
  }

  /**
   * Edit a horse in the system.
   *
   * @param horse the data for the horse that should be updated
   * @return an Observable for the updated horse
   */
  update(horse: Horse): Observable<Horse> {
    return this.http.put<Horse>(
      `${baseUri}/${horse.id}`,
      horse
    );
  }

  /**
   * Delete a horse in the system.
   *
   * @param horse the id of the horse whih should be deleted
   * @return an Observable for the delete completion
   */
  delete(id: number): Observable<void> {
    return this.http.delete<void>(
      `${baseUri}/${id}`
    );
  }

}
