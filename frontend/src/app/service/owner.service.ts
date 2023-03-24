import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { Owner } from '../dto/owner';

const baseUri = environment.backendUrl + '/owners';

/**
 * REST service to access horses from the backend
 */
@Injectable({
  providedIn: 'root',
})
export class OwnerService {
  constructor(private http: HttpClient) { }

  /**
   * Search a owner that matches a name
   *
   * @param name a string that has to be included in the owner name
   * @param limitTo tha maximum amount of owners to get
   * @returns an observable containing matching owners
   */
  public searchByName(name: string, limitTo: number): Observable<Owner[]> {
    const params = new HttpParams().set('name', name).set('maxAmount', limitTo);
    return this.http.get<Owner[]>(baseUri, { params });
  }

  /**
   * Gets all owners
   *
   * @returns an observable containing the list of owners
   */
  public getAll(): Observable<Owner[]> {
    return this.http.get<Owner[]>(baseUri);
  }

  /**
   * Create a new owner
   *
   * @param owner the data used to create a new owner
   * @returns an observable which resolves to the new owner
   */
  public create(owner: Owner): Observable<Owner> {
    return this.http.post<Owner>(baseUri, owner);
  }
}
