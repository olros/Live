import { ITeamCompact } from './Team';

export enum EPosition {
  KEEPER = 'Keeper',
  DEFENDER = 'Defender',
  MIDFIELDER = 'Midfielder',
  FORWARD = 'Forward',
}

export interface IPlayer {
  id: number;
  name: string;
  position: EPosition;
  number?: number;
  active: boolean;
  team: ITeamCompact;
}

export interface IPlayerCompact {
  id: number;
  name: string;
  position: EPosition;
  number?: number;
  active: boolean;
}

export interface ICreatePlayer {
  name: string;
  position: EPosition;
  number?: number;
  active: boolean;
}

export interface IUpdatePlayer {
  name?: string;
  position?: EPosition;
  number?: number;
  active?: boolean;
}
