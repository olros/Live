import { API_FIXTURE } from 'constant';
import { IFetch } from 'api/fetch';
import { ICreateFixture, IFixture, IFixtureCompact, IUpdateFixture } from 'types/Fixture';
import { SuccessResponse } from 'types/Request';

export default {
  getAllFixtures: () => IFetch<Array<IFixtureCompact>>('GET', API_FIXTURE),
  getFixtureById: (id: number) => IFetch<IFixture>('GET', `${API_FIXTURE}/${id}`),
  createFixture: (newFixture: ICreateFixture) => IFetch<IFixture>('POST', API_FIXTURE, newFixture),
  updateFixtureById: (id: number, updatedFixture: IUpdateFixture) => IFetch<IFixture>('PUT', `${API_FIXTURE}/${id}`, updatedFixture),
  deleteFixtureById: (id: number) => IFetch<SuccessResponse>('DELETE', `${API_FIXTURE}/${id}`),
};
