import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {HorseCreateEditComponent, HorseCreateEditMode} from './component/horse/horse-create-edit/horse-create-edit.component';
import {HorseComponent} from './component/horse/horse.component';

const routes: Routes = [
  {path: '', redirectTo: 'horses', pathMatch: 'full'},
  {path: 'horses', children: [
    {path: '', component: HorseComponent},
    {path: 'create', component: HorseCreateEditComponent, data: {mode: HorseCreateEditMode.create}},
    {path: 'edit/:id', component: HorseCreateEditComponent, data: {mode: HorseCreateEditMode.edit}},
  ]},
  {path: '**', redirectTo: 'horses'},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
