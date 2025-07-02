import http from 'k6/http';
import { Rate } from 'k6/metrics';

const failureRate = new Rate('failed_requests');

export function test_api_endpoints_config() {
    let res = http.get('http://localhost:8080/books');
    failureRate.add(res.status !== 200);
}
