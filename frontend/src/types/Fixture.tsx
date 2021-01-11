import { EPosition, IPlayer, IPlayerCompact } from './Player';
import { ISeasonCompact } from './Season';
import { ITeamCompact } from './Team';

export enum EFixtureEvent {
  GOAL,
  RED_CARD,
  YELLOW_CARD,
  FOUL,
  SUBSTITUTION,
  CORNER,
  OFFSIDE,
  COMMENTARY,
  FIXTURE_START,
  FIXTURE_END,
  PAUSE_START,
  PAUSE_END,
  INFO,
  OTHER,
}

export interface IFixtureResult {
  homeTeam: number;
  awayTeam: number;
}

export interface IFixture {
  id: number;
  location?: string;
  referee?: string;
  time: string;
  homeTeam: ITeamCompact;
  awayTeam: ITeamCompact;
  season: ISeasonCompact;
  players: Array<IPlayerCompact>;
  result: IFixtureResult;
}

export interface IFixtureCompact {
  id: number;
  time: string;
  homeTeam: ITeamCompact;
  awayTeam: ITeamCompact;
  result: IFixtureResult;
}

export interface ICreateFixture {
  location?: string;
  referee?: string;
  time: string;
  homeTeam: number;
  awayTeam: number;
  seasonId: number;
}

export interface IUpdateFixture {
  location?: string;
  referee?: string;
  time?: string;
  homeTeam?: number;
  awayTeam?: number;
}

export interface IParticipants {
  team?: ITeamCompact;
  goalScorer?: IPlayerCompact;
  assistant?: IPlayerCompact;
  playerIn?: IPlayerCompact;
  playerOut?: IPlayerCompact;
  player?: IPlayerCompact;
}

export interface IFixtureEvent {
  id: number;
  minute: number;
  type: EFixtureEvent;
  fixture: IFixtureCompact;
  text?: string;
  participants: IParticipants;
}

export interface IFixtureEventCompact {
  id: number;
  minute: number;
  type: EFixtureEvent;
  text?: string;
  participants: IParticipants;
}

export interface ICreateFixtureEvent {
  minute: number;
  type: EFixtureEvent;
  text?: string;
  player1?: number;
  player2?: number;
  team?: number;
}

export interface IUpdateFixtureEvent {
  minute?: number;
  type?: EFixtureEvent;
  text?: string;
  player1?: number;
  player2?: number;
  team?: number;
}

export interface IFixturePlayer {
  id: number;
  player: IPlayer;
  fixture: IFixtureCompact;
  number?: number;
  position: EPosition;
}

export interface IFixturePlayerCompact {
  id: number;
  player: IPlayer;
  number?: number;
  position: EPosition;
}

export interface ICreateFixturePlayer {
  playerId: number;
  number?: number;
  position?: EPosition;
}

export interface IUpdateFixturePlayer {
  number?: number;
  position?: EPosition;
}
