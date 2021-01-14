import { API_EVENTS, API_FIXTURES, API_PLAYERS } from 'constant';
import IFetch from 'api/fetch';
import {
  ICreateFixture,
  IFixture,
  IFixtureCompact,
  IUpdateFixture,
  ICreateFixtureEvent,
  ICreateFixturePlayer,
  IFixtureEvent,
  IFixtureEventCompact,
  IFixturePlayer,
  IFixturePlayerCompact,
  IUpdateFixtureEvent,
  IUpdateFixturePlayer,
} from 'types/Fixture';
import { SuccessResponse } from 'types/Request';

export default {
  getAllFixtures: (authToken?: string | undefined) => IFetch<Array<IFixtureCompact>>({ method: 'GET', url: API_FIXTURES, authToken }),
  getFixture: (fixtureId: number, authToken?: string | undefined) => IFetch<IFixture>({ method: 'GET', url: `${API_FIXTURES}/${fixtureId}`, authToken }),
  createFixture: (newFixture: ICreateFixture) => IFetch<IFixture>({ method: 'POST', url: API_FIXTURES, data: newFixture }),
  updateFixture: (fixtureId: number, updatedFixture: IUpdateFixture) =>
    IFetch<IFixture>({ method: 'PUT', url: `${API_FIXTURES}/${fixtureId}`, data: updatedFixture }),
  deleteFixture: (fixtureId: number) => IFetch<SuccessResponse>({ method: 'DELETE', url: `${API_FIXTURES}/${fixtureId}` }),

  getAllFixtureEvents: (fixtureId: number, authToken?: string | undefined) =>
    IFetch<Array<IFixtureEventCompact>>({ method: 'GET', url: `${API_FIXTURES}/${fixtureId}/${API_EVENTS}`, authToken }),
  getFixtureEvent: (fixtureId: number, eventId: number, authToken?: string | undefined) =>
    IFetch<IFixtureEvent>({ method: 'GET', url: `${API_FIXTURES}/${fixtureId}/${API_EVENTS}/${eventId}`, authToken }),
  createFixtureEvent: (fixtureId: number, newFixtureEvent: ICreateFixtureEvent) =>
    IFetch<IFixtureEvent>({ method: 'POST', url: `${API_FIXTURES}/${fixtureId}/${API_EVENTS}`, data: newFixtureEvent }),
  updateFixtureEvent: (fixtureId: number, eventId: number, updatedFixtureEvent: IUpdateFixtureEvent) =>
    IFetch<IFixtureEvent>({ method: 'PUT', url: `${API_FIXTURES}/${fixtureId}/${API_EVENTS}/${eventId}`, data: updatedFixtureEvent }),
  deleteFixtureEvent: (fixtureId: number, eventId: number) =>
    IFetch<SuccessResponse>({ method: 'DELETE', url: `${API_FIXTURES}/${fixtureId}/${API_EVENTS}/${eventId}` }),

  getAllFixturePlayers: (fixtureId: number, authToken?: string | undefined) =>
    IFetch<Array<IFixturePlayerCompact>>({ method: 'GET', url: `${API_FIXTURES}/${fixtureId}/${API_PLAYERS}`, authToken }),
  getFixturePlayer: (fixtureId: number, playerId: number, authToken?: string | undefined) =>
    IFetch<IFixturePlayer>({ method: 'GET', url: `${API_FIXTURES}/${fixtureId}/${API_PLAYERS}/${playerId}`, authToken }),
  createFixturePlayer: (fixtureId: number, newFixturePlayer: ICreateFixturePlayer) =>
    IFetch<IFixturePlayer>({ method: 'POST', url: `${API_FIXTURES}/${fixtureId}/${API_PLAYERS}`, data: newFixturePlayer }),
  updateFixturePlayer: (fixtureId: number, playerId: number, updatedFixturePlayer: IUpdateFixturePlayer) =>
    IFetch<IFixturePlayer>({ method: 'PUT', url: `${API_FIXTURES}/${fixtureId}/${API_PLAYERS}/${playerId}`, data: updatedFixturePlayer }),
  deleteFixturePlayer: (fixtureId: number, playerId: number) =>
    IFetch<SuccessResponse>({ method: 'DELETE', url: `${API_FIXTURES}/${fixtureId}/${API_PLAYERS}/${playerId}` }),
};
