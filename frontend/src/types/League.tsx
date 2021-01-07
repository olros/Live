import { ISeasonCompact } from './Season';
import { ITeamCompact } from './Team';

export interface ILeague {
  id: number;
  name: string;
  teams: Array<ITeamCompact>;
  seasons: Array<ISeasonCompact>;
  isAdmin: boolean;
}

export interface ILeagueCompact {
  id: number;
  name: string;
}

export interface ICreateLeague {
  name: string;
}

export interface IUpdateLeague {
  name?: string;
}

export interface IAddLeagueAdmin {
  email: string;
}
