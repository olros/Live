import { ITeamCompact } from './Team';

export interface ISeason {
  id: number;
  name: string;
  teams: Array<ITeamCompact>;
  isAdmin: boolean;
}

export interface ISeasonCompact {
  id: number;
  name: string;
}

export interface ICreateSeason {
  name: string;
  leagueId: number;
}

export interface IUpdateSeason {
  name?: string;
}

export interface IAddSeasonTeam {
  teamId: number;
}

export interface ITableEntry {
  team: ITeamCompact;
  goalsFor: number;
  goalsAgainst: number;
  wins: number;
  draws: number;
  losses: number;
  played: number;
  points: number;
  rank: number;
}
