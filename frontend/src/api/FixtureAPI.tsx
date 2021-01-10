import { API_FIXTURE } from 'constant';
import IFetch from 'api/fetch';
import { ICreateFixture, IFixture, IFixtureCompact, IUpdateFixture } from 'types/Fixture';
import { SuccessResponse } from 'types/Request';

export default {
  getAllFixtures: (authToken?: string | undefined) => IFetch<Array<IFixtureCompact>>({ method: 'GET', url: API_FIXTURE, authToken: authToken }),
  getFixtureById: (id: number, authToken?: string | undefined) => IFetch<IFixture>({ method: 'GET', url: `${API_FIXTURE}/${id}`, authToken: authToken }),
  createFixture: (newFixture: ICreateFixture, authToken?: string | undefined) =>
    IFetch<IFixture>({ method: 'POST', url: API_FIXTURE, data: newFixture, authToken: authToken }),
  updateFixtureById: (id: number, updatedFixture: IUpdateFixture, authToken?: string | undefined) =>
    IFetch<IFixture>({ method: 'PUT', url: `${API_FIXTURE}/${id}`, data: updatedFixture, authToken: authToken }),
  deleteFixtureById: (id: number, authToken?: string | undefined) =>
    IFetch<SuccessResponse>({ method: 'DELETE', url: `${API_FIXTURE}/${id}`, authToken: authToken }),
};
