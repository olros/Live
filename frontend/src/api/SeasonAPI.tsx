import { API_SEASONS, API_TEAMS, API_FIXTURES, API_TABLE } from 'constant';
import IFetch from 'api/fetch';
import { IFixtureCompact } from 'types/Fixture';
import { ITeamCompact } from 'types/Team';
import { ICreateSeason, IUpdateSeason, ISeason, IAddSeasonTeam, ITableEntry } from 'types/Season';
import { SuccessResponse } from 'types/Request';

export default {
  getSeasonById: (id: number, authToken?: string | undefined) => IFetch<ISeason>({ method: 'GET', url: `${API_SEASONS}/${id}`, authToken }),
  createSeason: (newSeason: ICreateSeason) => IFetch<ISeason>({ method: 'POST', url: API_SEASONS, data: newSeason }),
  updateSeasonById: (id: number, updatedseason: IUpdateSeason) => IFetch<ISeason>({ method: 'PUT', url: `${API_SEASONS}/${id}`, data: updatedseason }),
  deleteSeasonById: (id: number) => IFetch<SuccessResponse>({ method: 'DELETE', url: `${API_SEASONS}/${id}` }),

  addSeasonTeam: (id: number, newTeam: IAddSeasonTeam) =>
    IFetch<Array<ITeamCompact>>({ method: 'POST', url: `${API_SEASONS}/${id}/${API_TEAMS}`, data: newTeam }),
  getAllSeasonTeams: (id: number, authToken?: string | undefined) =>
    IFetch<Array<ITeamCompact>>({ method: 'GET', url: `${API_SEASONS}/${id}/${API_TEAMS}`, authToken }),
  deleteSeasonTeam: (seasonId: number, teamId: number) =>
    IFetch<Array<ITeamCompact>>({ method: 'DELETE', url: `${API_SEASONS}/${seasonId}/${API_TEAMS}/${teamId}` }),

  getSeasonFixtures: (seasonId: number, authToken?: string | undefined) =>
    IFetch<Array<IFixtureCompact>>({ method: 'GET', url: `${API_SEASONS}/${seasonId}/${API_FIXTURES}`, authToken }),
  getSeasonTable: (seasonId: number, authToken?: string | undefined) =>
    IFetch<Array<ITableEntry>>({ method: 'GET', url: `${API_SEASONS}/${seasonId}/${API_TABLE}`, authToken }),
};
