import { IPlayerCompact } from './Player';

export interface ITeam {
  id: number;
  name: string;
  logo?: string;
  description: string;
  isAdmin: boolean;
  players: Array<IPlayerCompact>;
}

export interface ITeamCompact {
  id: number;
  name: string;
  logo?: string;
  isAdmin: boolean;
}

export interface ICreateTeam {
  name: string;
  logo?: string;
  description: string;
  leagueId: number;
}

export interface IUpdateTeam {
  name?: string;
  logo?: string;
  description?: string;
}
