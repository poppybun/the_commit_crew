import yfinance as yf
import pandas as pd
from pathlib import Path

equity_tickers = ["AAPL", "MSFT", "GOOGL", "AMZN", "TSLA", "META", "NVDA", "NFLX", "TTWO", "BABA"]
etf_tickers = ["XLF", "QQQ", "SPY", "VTI", "DIA", "IWM", "XLV", "XLK", "XLE", "VNQ"]
bond_tickers = ["TLT", "SHY", "IEI", "IEF", "TIP", "VGIT", "TLH", "MUB", "LQD", "HYG"]

all_tickers = equity_tickers + etf_tickers + bond_tickers

ticker_class = {ticker: "equity" for ticker in equity_tickers}
ticker_class.update({ticker: "etf" for ticker in etf_tickers})
ticker_class.update({ticker: "bond" for ticker in bond_tickers})

DATA_DIR = Path(__file__).resolve().parent / "market_data"
DATA_DIR.mkdir(parents=True, exist_ok=True)

def fetch_price_history(tickers: list[str]) -> pd.DataFrame:
    raw = yf.download(
        tickers=tickers,
        period="10y",
        interval="1d",
        auto_adjust=True,
        progress=False,
        group_by="column",
        threads=True,
    )

    if raw.empty:
        raise ValueError("No price data returned from yfinance")

    if isinstance(raw.columns, pd.MultiIndex):
        prices = (
            raw.stack(level=1)
            .rename_axis(index=["date", "ticker"])
            .reset_index()
        )
    else:
        prices = raw.reset_index()
        prices["ticker"] = tickers[0]

    prices.columns = [str(col).lower().replace(" ", "_") for col in prices.columns]

    expected_columns = {"date", "ticker", "open", "high", "low", "close", "volume"}
    missing_columns = expected_columns - set(prices.columns)
    if missing_columns:
        raise ValueError(f"Price history is missing columns: {sorted(missing_columns)}")

    found_tickers = set(prices["ticker"].dropna())
    missing_tickers = [ticker for ticker in tickers if ticker not in found_tickers]
    if missing_tickers:
        raise ValueError(f"Price history is missing tickers: {missing_tickers}")

    prices["price_source"] = "yfinance"
    return prices

def fetch_ticker_metadata(tickers: list[str]) -> pd.DataFrame:
    rows = []

    for symbol in tickers:
        ticker = yf.Ticker(symbol)
        info = {}
        metadata_status = "success"
        metadata_error = ""

        try:
            info = ticker.get_info()
        except Exception as error:
            metadata_status = "failed"
            metadata_error = str(error)
            print(f"Could not fetch metadata for {symbol}: {metadata_error}")

        rows.append(
            {
                "ticker": symbol,
                "asset_class": ticker_class.get(symbol),
                "quote_type": info.get("quoteType"),
                "short_name": info.get("shortName"),
                "long_name": info.get("longName"),
                "sector": info.get("sector"),
                "industry": info.get("industry"),
                "category": info.get("category"),
                "fund_family": info.get("fundFamily"),
                "exchange": info.get("exchange"),
                "currency": info.get("currency"),
                "country": info.get("country"),
                "metadata_status": metadata_status,
                "metadata_error": metadata_error,
                "metadata_source": "yfinance",
            }
        )

    metadata = pd.DataFrame(rows)

    if len(metadata) != len(tickers):
        raise ValueError("Metadata row count does not match the number of requested tickers")

    return metadata

def save_dataframes(prices: pd.DataFrame, metadata: pd.DataFrame) -> None:
    prices.to_csv(DATA_DIR / "price_history.csv", index=False)
    metadata.to_csv(DATA_DIR / "ticker_metadata.csv", index=False)


def main() -> None:
    prices = fetch_price_history(all_tickers)
    metadata = fetch_ticker_metadata(all_tickers)
    save_dataframes(prices, metadata)

if __name__ == "__main__":
    main()
