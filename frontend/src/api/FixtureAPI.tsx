import { API_FIXTURES } from 'constant';
import IFetch from 'api/fetch';
import { ICreateFixture, IFixture, IFixtureCompact, IUpdateFixture } from 'types/Fixture';
import { SuccessResponse } from 'types/Request';

export default {
  getAllFixtures: (authToken?: string | undefined) => IFetch<Array<IFixtureCompact>>({ method: 'GET', url: API_FIXTURES, authToken }),
  getFixtureById: (id: number, authToken?: string | undefined) => IFetch<IFixture>({ method: 'GET', url: `${API_FIXTURES}/${id}`, authToken }),
  createFixture: (newFixture: ICreateFixture) => IFetch<IFixture>({ method: 'POST', url: API_FIXTURES, data: newFixture }),
  updateFixtureById: (id: number, updatedFixture: IUpdateFixture) => IFetch<IFixture>({ method: 'PUT', url: `${API_FIXTURES}/${id}`, data: updatedFixture }),
  deleteFixtureById: (id: number) => IFetch<SuccessResponse>({ method: 'DELETE', url: `${API_FIXTURES}/${id}` }),
};
