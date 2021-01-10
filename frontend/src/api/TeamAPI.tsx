import { API_TEAMS, API_ADMINS, API_PLAYERS } from 'constant';
import IFetch from 'api/fetch';
import { IUserCompact } from 'types/User';
import { IAddLeagueAdmin } from 'types/League';
import { ICreateTeam, IUpdateTeam, ITeam } from 'types/Team';
import { ICreatePlayer, IPlayer, IPlayerCompact, IUpdatePlayer } from 'types/Player';
import { SuccessResponse } from 'types/Request';

export default {
  getTeamById: (id: number, authToken?: string | undefined) => IFetch<ITeam>({ method: 'GET', url: `${API_TEAMS}/${id}`, authToken }),
  createTeam: (newTeam: ICreateTeam) => IFetch<ITeam>({ method: 'POST', url: API_TEAMS, data: newTeam }),
  updateTeamById: (id: number, updatedteam: IUpdateTeam) => IFetch<ITeam>({ method: 'PUT', url: `${API_TEAMS}/${id}`, data: updatedteam }),
  deleteTeamById: (id: number) => IFetch<SuccessResponse>({ method: 'DELETE', url: `${API_TEAMS}/${id}` }),

  addTeamAdmin: (id: number, newAdmin: IAddLeagueAdmin) =>
    IFetch<Array<IUserCompact>>({ method: 'POST', url: `${API_TEAMS}/${id}/${API_ADMINS}`, data: newAdmin }),
  getAllTeamAdmins: (id: number, authToken?: string | undefined) =>
    IFetch<Array<IUserCompact>>({ method: 'GET', url: `${API_TEAMS}/${id}/${API_ADMINS}`, authToken }),
  deleteTeamAdmin: (leagueId: number, userId: number) =>
    IFetch<Array<IUserCompact>>({ method: 'DELETE', url: `${API_TEAMS}/${leagueId}/${API_ADMINS}/${userId}` }),

  getAllTeamPlayers: (teamId: number, authToken?: string | undefined) =>
    IFetch<Array<IPlayerCompact>>({ method: 'GET', url: `${API_TEAMS}/${teamId}/${API_PLAYERS}`, authToken }),
  getTeamPlayerById: (teamId: number, playerId: number, authToken?: string | undefined) =>
    IFetch<IPlayer>({ method: 'GET', url: `${API_TEAMS}/${teamId}/${API_PLAYERS}/${playerId}`, authToken }),
  createTeamPlayer: (teamId: number, newPlayer: ICreatePlayer) =>
    IFetch<IPlayer>({ method: 'POST', url: `${API_TEAMS}/${teamId}/${API_PLAYERS}`, data: newPlayer }),
  updateTeamPlayer: (teamId: number, playerId: number, updatedPlayer: IUpdatePlayer) =>
    IFetch<IPlayer>({ method: 'PUT', url: `${API_TEAMS}/${teamId}/${API_PLAYERS}/${playerId}`, data: updatedPlayer }),
};
