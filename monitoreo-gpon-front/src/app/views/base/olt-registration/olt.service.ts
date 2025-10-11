import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class OltService {
  private apiUrl = '/api/olts'; // Substitua pela URL real da sua API

  constructor(private http: HttpClient) {}

  registerOlt(oltData: any): Observable<any> {
    return this.http.post(this.apiUrl, oltData);
  }

  // Você pode adicionar mais métodos aqui, como listar, atualizar, deletar OLTs, etc.
}
