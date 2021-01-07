import { ILeagueCompact } from './League';
import { ITeamCompact } from './Team';

export interface IUser {
  id: number;
  name: string;
  email: string;
  leagues: Array<ILeagueCompact>;
  teams: Array<ITeamCompact>;
}

export interface IUserCompact {
  id: number;
  name: string;
  email: string;
}

export interface ICreateUser {
  name: string;
  email: string;
  password: string;
}

export interface IUpdateUser {
  name?: string;
}
