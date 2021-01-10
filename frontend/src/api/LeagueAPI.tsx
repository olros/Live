import { API_LEAGUES, API_ADMINS } from 'constant';
import IFetch from 'api/fetch';
import { IUserCompact } from 'types/User';
import { ICreateLeague, IUpdateLeague, ILeague, ILeagueCompact, IAddLeagueAdmin } from 'types/League';
import { SuccessResponse } from 'types/Request';

export default {
  getAllLeagues: (authToken?: string | undefined) => IFetch<Array<ILeagueCompact>>({ method: 'GET', url: API_LEAGUES, authToken }),
  getLeagueById: (id: number, authToken?: string | undefined) => IFetch<ILeague>({ method: 'GET', url: `${API_LEAGUES}/${id}`, authToken }),
  createLeague: (newLeague: ICreateLeague) => IFetch<ILeague>({ method: 'POST', url: API_LEAGUES, data: newLeague }),
  updateLeagueById: (id: number, updatedLeague: IUpdateLeague) => IFetch<ILeague>({ method: 'PUT', url: `${API_LEAGUES}/${id}`, data: updatedLeague }),
  deleteLeagueById: (id: number) => IFetch<SuccessResponse>({ method: 'DELETE', url: `${API_LEAGUES}/${id}` }),

  addLeagueAdmin: (id: number, newAdmin: IAddLeagueAdmin) =>
    IFetch<Array<IUserCompact>>({ method: 'POST', url: `${API_LEAGUES}/${id}/${API_ADMINS}`, data: newAdmin }),
  getAllLeagueAdmins: (id: number, authToken?: string | undefined) =>
    IFetch<Array<IUserCompact>>({ method: 'GET', url: `${API_LEAGUES}/${id}/${API_ADMINS}`, authToken }),
  deleteLeagueAdmin: (leagueId: number, userId: number) =>
    IFetch<Array<IUserCompact>>({ method: 'DELETE', url: `${API_LEAGUES}/${leagueId}/${API_ADMINS}/${userId}` }),
};
