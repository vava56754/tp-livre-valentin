import http from 'k6/http';
import { Rate } from 'k6/metrics';

const failureRate = new Rate('failed_requests');

export function test_get_books() {
    let res = http.get('http://localhost:8080/books');
    failureRate.add(res.status !== 200);
}

export function test_create_book() {
    let payload = JSON.stringify({
        title: 'Test Book',
        author: 'Test Author'
    });
    let params = { headers: { 'Content-Type': 'application/json' } };
    let res = http.post('http://localhost:8080/books', payload, params);
    failureRate.add(res.status !== 201);
}

export function test_reserve_book() {
    let res = http.post('http://localhost:8080/books/reserve/1');
    failureRate.add(res.status !== 200);
}
