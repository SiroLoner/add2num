/*
 * Add2Num — điều khiển phần "tiến trình tính toán".
 *
 * Máy chủ đã tính xong và gửi kèm danh sách các cột trong thuộc tính data-steps.
 * Trang chỉ việc phát lại danh sách đó, nên không có lời gọi mạng nào ở đây và
 * kết quả hiển thị luôn khớp với kết quả đã tính.
 */
(function () {
    'use strict';

    document.addEventListener('DOMContentLoaded', function () {
        wireFormShortcuts();
        wireCopyButton();
        wireWalkthrough();
    });

    /* ------------------------------------------------------------------ form */

    function wireFormShortcuts() {
        var first = document.getElementById('firstNumber');
        var second = document.getElementById('secondNumber');
        var example = document.getElementById('btn-example');
        var clear = document.getElementById('btn-clear');

        if (example && first && second) {
            example.addEventListener('click', function () {
                first.value = '1234';
                second.value = '897';
                first.focus();
            });
        }
        if (clear && first && second) {
            clear.addEventListener('click', function () {
                first.value = '';
                second.value = '';
                first.focus();
            });
        }
    }

    function wireCopyButton() {
        var button = document.getElementById('btn-copy');
        if (!button) {
            return;
        }
        button.addEventListener('click', function () {
            var value = button.getAttribute('data-value') || '';
            var done = function () {
                var original = button.textContent;
                button.textContent = 'Đã sao chép';
                window.setTimeout(function () {
                    button.textContent = original;
                }, 1500);
            };

            if (navigator.clipboard && window.isSecureContext) {
                navigator.clipboard.writeText(value).then(done, fallbackCopy);
            } else {
                fallbackCopy();
            }

            // Trình duyệt cũ hoặc trang chạy qua http thuần thì không có clipboard API.
            function fallbackCopy() {
                var area = document.createElement('textarea');
                area.value = value;
                area.setAttribute('readonly', '');
                area.style.position = 'fixed';
                area.style.opacity = '0';
                document.body.appendChild(area);
                area.select();
                try {
                    document.execCommand('copy');
                    done();
                } catch (ignored) {
                    /* không sao chép được thì thôi, người dùng vẫn bôi đen tay được */
                } finally {
                    document.body.removeChild(area);
                }
            }
        });
    }

    /* ----------------------------------------------------------- walkthrough */

    function wireWalkthrough() {
        var data = document.getElementById('steps-data');
        var tbody = document.getElementById('step-rows');
        var bar = document.getElementById('progress-bar');
        var label = document.getElementById('progress-label');
        if (!data || !tbody || !bar || !label) {
            return;
        }

        var steps = parseSteps(data.getAttribute('data-steps'));
        var shown = 0;
        var timer = null;

        var playButton = document.getElementById('btn-play');
        var stepButton = document.getElementById('btn-step');
        var resetButton = document.getElementById('btn-reset');
        var allButton = document.getElementById('btn-all');

        render();

        if (playButton) {
            playButton.addEventListener('click', togglePlay);
        }
        if (stepButton) {
            stepButton.addEventListener('click', function () {
                pause();
                advance(1);
            });
        }
        if (resetButton) {
            resetButton.addEventListener('click', function () {
                pause();
                shown = 0;
                render();
            });
        }
        if (allButton) {
            allButton.addEventListener('click', function () {
                pause();
                shown = steps.length;
                render();
            });
        }

        // Số cột ít thì hiện luôn; nhiều thì để người dùng bấm chạy.
        if (steps.length > 0 && steps.length <= 12) {
            shown = steps.length;
            render();
        }

        function parseSteps(raw) {
            if (!raw) {
                return [];
            }
            try {
                var parsed = JSON.parse(raw);
                return Array.isArray(parsed) ? parsed : [];
            } catch (error) {
                return [];
            }
        }

        function togglePlay() {
            if (timer) {
                pause();
                return;
            }
            if (shown >= steps.length) {
                shown = 0;
                render();
            }
            playButton.textContent = 'Tạm dừng';
            // Nhiều cột thì chạy nhanh hơn để không phải chờ quá lâu.
            var interval = steps.length > 120 ? 20 : (steps.length > 40 ? 60 : 220);
            timer = window.setInterval(function () {
                advance(1);
                if (shown >= steps.length) {
                    pause();
                }
            }, interval);
        }

        function pause() {
            if (timer) {
                window.clearInterval(timer);
                timer = null;
            }
            if (playButton) {
                playButton.textContent = 'Chạy';
            }
        }

        function advance(count) {
            shown = Math.min(steps.length, shown + count);
            render();
        }

        function render() {
            tbody.replaceChildren();

            var fragment = document.createDocumentFragment();
            for (var i = 0; i < shown; i++) {
                fragment.appendChild(buildRow(steps[i], i === shown - 1));
            }
            tbody.appendChild(fragment);

            var percent = steps.length === 0 ? 0 : Math.round((shown / steps.length) * 100);
            bar.style.width = percent + '%';
            bar.parentNode.setAttribute('aria-valuenow', String(percent));
            label.textContent = shown + ' / ' + steps.length + ' cột';

            var current = tbody.querySelector('tr.step-current');
            if (current && shown > 0 && shown < steps.length) {
                current.scrollIntoView({block: 'nearest'});
            }
        }

        function buildRow(step, isCurrent) {
            var row = document.createElement('tr');
            if (isCurrent) {
                row.className = 'step-current';
            }
            if (step.carryOut === 1) {
                row.className = row.className ? row.className + ' step-carry' : 'step-carry';
            }

            appendCell(row, step.column, 'text-end');
            appendCell(row, step.place, '');
            appendCell(row, step.leftDigit, 'text-end font-monospace');
            appendCell(row, step.rightDigit, 'text-end font-monospace');
            appendCell(row, step.carryIn, 'text-end font-monospace');
            appendCell(row, step.total, 'text-end font-monospace fw-semibold');
            appendCell(row, step.digit, 'text-end font-monospace fw-semibold');
            appendCell(row, step.carryOut, 'text-end font-monospace');
            return row;
        }

        function appendCell(row, value, className) {
            var cell = document.createElement('td');
            if (className) {
                cell.className = className;
            }
            // textContent, không phải innerHTML: dữ liệu đi thẳng vào DOM mà không diễn giải HTML.
            cell.textContent = String(value);
            row.appendChild(cell);
        }
    }
}());
