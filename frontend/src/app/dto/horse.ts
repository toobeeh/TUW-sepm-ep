import { Owner } from './owner';
import { Sex } from './sex';

export interface Horse {
  id?: number;
  name: string;
  description?: string;
  dateOfBirth: Date;
  sex: Sex;
  owner?: Owner;
  mother?: Horse;
  father?: Horse;
}

export interface HorseSearch {
  name?: string;
  description?: string;
  bornBefore?: string;
  sex?: Sex;
  ownerName?: string;
}
